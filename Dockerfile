FROM ubuntu:latest
LABEL authors="migueljorquera"

ENTRYPOINT ["top", "-b"]