#!/usr/bin/env bash
set -euo pipefail

#################################
# Usage & Environment Validation
#################################
ENV="${1:-}"
ALLOWED_ENVS=("dev" "qa" "prod")
if [[ ! " ${ALLOWED_ENVS[@]} " =~ " ${ENV} " ]]; then
  echo "Usage: $0 <dev|qa|prod>"
  exit 1
fi

#################################
# Config
#################################
NAMESPACE="user-service-${ENV}"
DEPLOYMENT_NAME="user-service"
CONTAINER_NAME="user"
IMAGE="ghcr.io/${GITHUB_USER:-CHANGEME}/user-service:latest"

CONFIGMAP_FILE="k8s/configmap-${ENV}.yaml"
SECRET_FILE="k8s/secret-${ENV}.yaml"

# Validation for ConfigMap & Secret files
for FILE in "$CONFIGMAP_FILE" "$SECRET_FILE"; do
  if [[ ! -f "$FILE" ]]; then
    echo "ERROR: Missing $FILE (generate or supply before deploying)"
    exit 1
  fi
done

if [[ -z "${GHCR_TOKEN:-}" ]]; then
  echo "ERROR: GHCR_TOKEN environment variable not set."
  echo "Run: export GHCR_TOKEN=<your_pat>"
  exit 1
fi

#################################
# Display Deployment Targets
#################################
echo ""
echo "Deploying to: $ENV"
echo "--------------------------------------"
echo "Namespace       : $NAMESPACE"
echo "Deployment Name : $DEPLOYMENT_NAME"
echo "Container Name  : $CONTAINER_NAME"
echo "Image           : $IMAGE"
echo "ConfigMap       : $CONFIGMAP_FILE"
echo "Secret          : $SECRET_FILE"
echo "--------------------------------------"
echo ""

#################################
# Apply ConfigMap & Secret
#################################
echo "Applying environment ConfigMap..."
kubectl apply -f "$CONFIGMAP_FILE" -n "${NAMESPACE}"

echo "Applying environment Secret..."
kubectl apply -f "$SECRET_FILE" -n "${NAMESPACE}"

#################################
# Apply Core Manifests
#################################
echo "Applying Kubernetes manifests (deployment, service, ingress)..."
kubectl apply -f k8s/deployment.yaml -n "${NAMESPACE}"
kubectl apply -f k8s/service.yaml -n "${NAMESPACE}"
kubectl apply -f k8s/ingress.yaml -n "${NAMESPACE}"

#################################
# Ensure GHCR Pull Secret
#################################
echo "Ensuring GHCR pull secret exists..."
kubectl create secret docker-registry ghcr-secret \
  --docker-server=ghcr.io \
  --docker-username="${GITHUB_USER:-CHANGEME}" \
  --docker-password="${GHCR_TOKEN}" \
  --docker-email="noemail@example.com" \
  --namespace "${NAMESPACE}" \
  --dry-run=client -o yaml | kubectl apply -f -

#################################
# Update Deployment Image
#################################
echo "Updating deployment image..."
kubectl -n "${NAMESPACE}" set image deployment/"${DEPLOYMENT_NAME}" "${CONTAINER_NAME}"="${IMAGE}" --record

#################################
# Rollout Status & Results
#################################
echo "Waiting for rollout..."
if ! kubectl -n "${NAMESPACE}" rollout status deployment/"${DEPLOYMENT_NAME}" --timeout=180s; then
  echo "ERROR: Deployment rollout failed!"
  exit 1
fi

echo ""
echo "Deployment to '$ENV' complete! Current pods:"
kubectl -n "${NAMESPACE}" get pods -o wide
kubectl -n "${NAMESPACE}" get deployment "${DEPLOYMENT_NAME}" -o=jsonpath='{.spec.template.spec.containers[0].image}'; echo ""

#################################
# Done
#################################
exit 0
