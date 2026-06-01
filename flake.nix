{
  description = "Statlog: eBPF-based system monitor";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { self, nixpkgs }: let
    # List of systems we want to support building for
    supportedSystems = [ "x86_64-linux" "aarch64-linux" ];
    
    # A helper function to generate the packages attribute for each system
    # This replaces the need for the flake-utils dependency
    forAllSystems = nixpkgs.lib.genAttrs supportedSystems;
  in {
    packages = forAllSystems (system: let
      pkgs = nixpkgs.legacyPackages.${system};
    in {
      default = pkgs.clangStdenv.mkDerivation {
        pname = "statlog";
        version = "0.1.0";
        src = ./.;

        nativeBuildInputs = with pkgs; [
          cmake
          bpftools
          llvmPackages.clang
          llvmPackages.llvm
        ];

        buildInputs = with pkgs; [
          libbpf
          elfutils
          zlib
        ];

        # hardening flags don't work when compiling bpf .o file?
        hardeningDisable = [ "all" ];

        # Use the setup hook to properly initialize cmake flags
        preConfigure = ''
          # Find the vmlinux file. We search in linux.dev which often contains the ELF vmlinux.
          export BTF_PATH=$(find ${pkgs.linux.dev} -maxdepth 1 -name vmlinux -type f -print -quit 2>/dev/null || true)
          export BPFCPU="v3"
        '';

        meta = with pkgs.lib; {
          description = "A simple system monitor using eBPF";
          license = licenses.mit;
          platforms = supportedSystems;
          mainProgram = "statlog";
        };
      };
    });
  };
}
