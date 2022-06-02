#!/usr/bin/env bash

# osX: readlink -f does not work, therefore such construction needs to be used
base_dir="$(cd "$(dirname "$0")" && pwd -P)"
echo "Rebuilding BOM in ${base_dir}"

pushd "${base_dir}"/.. >/dev/null || exit 1

projects=$(mvn -Dexec.executable='echo' -Dexec.args='<dependency><groupId>${project.groupId}</groupId><artifactId>${project.artifactId}</artifactId><version>${project.version}</version></dependency>' exec:exec -q | sort | tr '\n' ' ')

pushd "${base_dir}" >/dev/null || exit 1
cp -f pom.xml.template pom.xml
# osX: sed -i has extension parameter mandatory
sed -i.bak "s#<\/dependencies>#${projects}</dependencies>#" pom.xml && rm pom.xml.bak
