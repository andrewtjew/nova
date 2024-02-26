..\..\nssm stop "Evolve crs"
..\..\nssm remove "Evolve crs" confirm
..\..\nssm install "Evolve crs" "c:\test-all-local\crs\run-test-all-local.bat"
..\..\nssm set "Evolve crs" Start SERVICE_DELAYED_AUTO_START
..\..\nssm start "Evolve crs"
