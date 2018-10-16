#!/bin/bash

set -euo pipefail

url=$1
destination=$1

mkdir -p ${destination}
cd ${destination}
curl -OJ ${url} ${destination}
echo "Downloaded ${url} to ${destination}"