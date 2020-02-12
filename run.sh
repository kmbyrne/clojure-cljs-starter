export DOCKER_BUILDKIT=1
docker image rm "ccs-server" -f
cd server || exit
docker build . -t ccs-server
cd ../ui || exit
docker build . -t ccs-ui
cd ..
docker-compose up
