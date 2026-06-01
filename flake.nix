{
  description = "Statlog: eBPF-based system monitor";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { self, nixpkgs }: {
    # We specify x86_64-linux directly to remove the 'flake-utils' dependency.
    packages.x86_64-linux.default = let
      pkgs = nixpkgs.legacyPackages.x86_64-linux;
    in pkgs.stdenv.mkDerivation {
      pname = "statlog";
      version = "0.1.0";
      src = ./.;

      # Tools used ONLY at build time
      nativeBuildInputs = with pkgs; [
        cmake
        bpftool
        clang
        llvm
      ];

      # Libraries linked into the final binary
      buildInputs = with pkgs; [
        libbpf
        elfutils
        zlib
      ];

      preConfigure = ''
        pushd src/bpf
        # Find the vmlinux file in the Nix store to generate vmlinux.h
        VMLINUX=$(ls ${pkgs.linux}/vmlinux ${pkgs.linux.dev}/lib/modules/*/build/vmlinux 2>/dev/null | head -n 1)
        ./compile.sh "$VMLINUX"
        popd
      '';

      installPhase = ''
        mkdir -p $out/bin
        cp statlog $out/bin/
      '';

      meta = with pkgs.lib; {
        description = "A simple system monitor using eBPF";
        license = licenses.mit;
        platforms = [ "x86_64-linux" ];
        mainProgram = "statlog";
      };
    };
  };
}
