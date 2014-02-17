#!/usr/bin/env bash
#

for sub in scct sbt-scct xsbt-coveralls-plugin; do
  ( cd $sub && sbt +publishLocal )
done

sbt scct:test coveralls
