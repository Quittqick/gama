cd ummisco.gama.annotations &&
mvn install &&
cd - &&
cd msi.gama.processor &&
mvn install &&
cd - &&
cd msi.gama.parent &&
mvn  clean install &&
cd -

