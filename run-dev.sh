export DOCKER_BUILDKIT=1
docker image rm "ccs-server" -f
docker build ./server -t ccs-server
docker run -p 8080:8080 -p 3001:3001 ccs-server &
cd ui || exit
#npm run dev
