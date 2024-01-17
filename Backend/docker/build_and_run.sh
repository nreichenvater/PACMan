#!/bin/bash

run_start()
{
echo -e "\e[1;35m -> Running Analytics Dashboard Docker Stack... \e[0m"
docker-compose -f docker_compose.yaml -p competence_susi up
}


run_build(){
docker stop competence_susi_db
docker stop competence_mapper
docker rm competence_susi_db
docker rm competence_mapper

echo -e "\e[1;35m -> Docker Container Stopped and Removed! \e[0m"
sleep 5
echo -e "\e[1;35m -> Building Dashboard image... \e[0m"
cd ..
docker build -f dockerfile --tag competence .
cd docker
}

run_down(){
echo -e "\e[1;35m --- Analytics Dashboard Remove --- \e[0m"
docker-compose -f docker_compose.yaml down 
}



 if [ "$#" -eq  "0" ]
   then
    run_start
 elif [[ "$1" == *"build"* ]]
 	then
 	run_down
 	run_build
 	run_start
 elif [[ "$1" == *"down"* ]]
 	then
 	run_down
 else
     echo "Wrong argument!\n- Run without argument for docker compose up | 'down' for down  | 'build' for a full clean build of the images"
     exit 1
 fi


