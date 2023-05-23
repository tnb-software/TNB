#!/usr/bin/env bash

# osX: readlink -f does not work, therefore such construction needs to be used
base_dir="$(cd "$(dirname "$0")" && pwd -P)"
echo "Rebuilding POM in ${base_dir}"

pushd "${base_dir}"/.. >/dev/null || exit 1

# This runs inside system-x folder
projects=$(mvn --projects !:system-x-all -Dexec.executable='echo' -Dexec.args='${project.packaging}:${project.groupId}:${project.artifactId}' exec:exec -q | sort)
jars=""
while read -r line; do
  IFS=: read -r packaging group_id artifact_id <<< "${line}"
  # for dependencies, only jar packaging is valid
  if [ "${packaging}" = "jar" ]; then
    jars="${jars}<dependency><groupId>${group_id}</groupId><artifactId>${artifact_id}</artifactId></dependency>"
  fi
done <<<"${projects}"

pushd "${base_dir}" >/dev/null || exit 1
# delete newlines in the template, so that sed can replace second occurence of "/dependencies" string
tr -d '\n' < pom.xml.template | sed "s#<\/dependencies>#${jars}</dependencies>#2" > pom.xml
