#!/bin/bash

./mvnw clean package && docker build -t metrics-manager .
