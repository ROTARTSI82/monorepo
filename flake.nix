{
  description = "Statlog: lightweight system monitor";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { self, nixpkgs }: let
    pkgs = nixpkgs.legacyPackages."x86_64-linux";
  in {
    packages."x86_64-linux".default = pkgs.clangStdenv.mkDerivation {
      pname = "statlog";
      version = "0.1.0";
      src = ./.;

      nativeBuildInputs = with pkgs; [
        cmake
      ];

      meta = with pkgs.lib; {
        description = "A simple system monitor";
        license = licenses.mit;
        mainProgram = "statlog";
      };
    };
  };
}
