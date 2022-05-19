demo_dir="${PWD}/demo/"
cd "${demo_dir}"
./gradlew clean build

./gradlew clean build
docker build -t paullee714/testapp .
docker push paullee714/testapp
