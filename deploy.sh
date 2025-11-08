#!/usr/bin/env bash
set -e

#################################
# CHECK ARGUMENT
#################################
ENV="$1"

if [[ -z "$ENV" ]]; then
  echo "ERROR: Missing environment argument."
  echo "Usage: ./deploy.sh <dev|qa|prod>"
  exit 1
fi

if [[ "$ENV" != "dev" && "$ENV" != "qa" && "$ENV" != "prod" ]]; then
  echo "ERROR: Invalid environment '$ENV'"
  echo "Allowed: dev, qa, prod"
  exit 1
fi

#################################
# CONFIG
#################################
NAMESPACE="default"
DEPLOYMENT_NAME="user-service"
CONTAINER_NAME="user"
IMAGE="ghcr.io/${GITHUB_USER}/user-service:latest"

CONFIGMAP_FILE="k8s/configmap-${ENV}.yaml"

if [[ ! -f "$CONFIGMAP_FILE" ]]; then
  echo "ERROR: Missing ConfigMap file: $CONFIGMAP_FILE"
  exit 1
fi

if [[ -z "$GHCR_TOKEN" ]]; then
  echo "ERROR: GHCR_TOKEN environment variable not set."
  echo "Set it once: export GHCR_TOKEN=<your_pat>"
  exit 1
fi

echo ""
echo "Deploying to environment: $ENV"
echo "-----------------------------------------"
echo "Namespace       : $NAMESPACE"
echo "Deployment Name : $DEPLOYMENT_NAME"
echo "Container Name  : $CONTAINER_NAME"
echo "Image           : $IMAGE"
echo "ConfigMap       : $CONFIGMAP_FILE"
echo "-----------------------------------------"
echo ""

#################################
# APPLY CONFIGMAP
#################################
echo "Applying environment-specific ConfigMap: $CONFIGMAP_FILE"
kubectl apply -f "$CONFIGMAP_FILE"

#################################
# APPLY COMMON K8s MANIFESTS
#################################
echo ""
echo "Applying Kubernetes manifests (deployment, service, ingress)..."
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml

#################################
# ENSURE GHCR SECRET
#################################
echo ""
echo "Ensuring GHCR pull secret exists..."
kubectl create secret docker-registry ghcr-secret \
  --docker-server=ghcr.io \
  --docker-username=${GITHUB_USER} \
  --docker-password=${GHCR_TOKEN} \
  --docker-email=noemail@example.com \
  --namespace ${NAMESPACE} \
  --dry-run=client -o yaml | kubectl apply -f -

#################################
# UPDATE IMAGE
#################################
echo ""
echo "Updating deployment image..."
kubectl -n ${NAMESPACE} set image deployment/${DEPLOYMENT_NAME} ${CONTAINER_NAME}=${IMAGE} --record

#################################
# WAIT FOR ROLLOUT
#################################
echo ""
echo "Waiting for rollout..."
kubectl -n ${NAMESPACE} rollout status deployment/${DEPLOYMENT_NAME} --timeout=180s

#################################
# DONE
#################################
echo ""
echo "Deployment to '$ENV' complete!"
echo ""
kubectl -n ${NAMESPACE} get pods -o wide
