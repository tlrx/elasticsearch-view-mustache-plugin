#!/bin/bash

ES_HOST=localhost:9200

echo Delete all
curl -XDELETE "http://$ES_HOST/"

echo
echo Creating index catalog
curl -XPOST "http://$ES_HOST/catalog"

echo
echo Updating product mapping
curl -XPUT "http://$ES_HOST/catalog/product/_mapping" --data-binary @org/elasticsearch/test/integration/views/mustache/mappings/product.json

echo
echo Bulk indexing products
curl -XPOST "http://$ES_HOST/catalog/product/_bulk" --data-binary @org/elasticsearch/test/integration/views/mustache/data/products.json

echo
echo Refresh all
curl -XPOST "http://$ES_HOST/_refresh"


echo
echo ----- Views -------
curl -XGET "http://$ES_HOST/_view/catalog/product/1"