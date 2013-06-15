find . -name lib -exec rm -rf {} \;
find . -name output -exec rm -rf {} \;
find . -name target -exec rm -rf {} \;
cd ..
zip -rp rest-demo.zip ./rest-demo
