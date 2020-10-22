export AZURE_SCHEMA_URL=https://$AZURE_DNS_NAME/api/schema-service/v1/schema
BEARER_TOKEN=`python $AZURE_DEPLOYMENTS_SUBDIR/Token.py`
export BEARER_TOKEN=$BEARER_TOKEN
python deployments/scripts/DeploySharedSchemas.py -u $AZURE_SCHEMA_URL
