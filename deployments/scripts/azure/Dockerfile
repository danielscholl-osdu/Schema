FROM mcr.microsoft.com/azure-cli:2.62.0

RUN apk add ca-certificates bash curl wget gettext jq bind-tools \
    && wget -q https://storage.googleapis.com/kubernetes-release/release/v1.21.2/bin/linux/amd64/kubectl -O /usr/local/bin/kubectl \
    && chmod +x /usr/local/bin/kubectl \
    && chmod g+rwx /root \
    && mkdir /config \
    && chmod g+rwx /config

ENV DATA_PARTITION="opendes" \
  AZURE_BUILD_SUBDIR="provider/schema-azure" \
  AZURE_TEST_SUBDIR="testing/schema-test-core" \
  AZURE_DEPLOYMENTS_SUBDIR="deployments/scripts/azure" \
  AZURE_DEPLOYMENTS_SCRIPTS_SUBDIR="deployments/scripts" \
  AZURE_DNS_NAME=$AZURE_DNS_NAME \
  AZURE_STORAGE_CONNECTION_STRING="DefaultEndpointsProtocol=https;AccountName=${AZURE_BASE}data;AccountKey=${AZURE_STORAGE_KEY};EndpointSuffix=core.windows.net"

USER root

# Create osdu user
RUN addgroup --system osdu && adduser --system osdu --ingroup osdu
WORKDIR /home/osdu

# Install Requirements
ADD deployments/scripts/azure/requirements.txt ./requirements.txt
RUN pip install -r requirements.txt

# Add Scripts
COPY deployments deployments
RUN chmod +x deployments/scripts/azure/bootstrap.sh

USER osdu

ENTRYPOINT ["/bin/bash", "/home/osdu/deployments/scripts/azure/bootstrap.sh"]
