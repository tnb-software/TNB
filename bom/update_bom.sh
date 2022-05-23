#!/usr/bin/env bash

base_dir=$(dirname "$(readlink -f "$0")")
echo "Rebuilding BOM"

pushd "${base_dir}"/.. >/dev/null || exit 1

projects=$(mvn --projects !bom -Dexec.executable='echo' -Dexec.args='<dependency><groupId>${project.groupId}</groupId><artifactId>${project.artifactId}</artifactId><version>${project.version}</version></dependency>' exec:exec -q | sort | tr '\n' ' ')

pushd "${base_dir}" >/dev/null || exit 1
cp -f pom.xml.template pom.xml
sed -i "s#<\/dependencies>#${projects}</dependencies>#" pom.xml
