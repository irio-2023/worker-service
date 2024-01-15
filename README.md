# WeWu worker service

## GKE config

```bash
# secret config
gcloud container clusters get-credentials <cluster-name> --location <cluster-location>
kubectl apply -f secret.yaml

# custom metrics config
gcloud iam service-accounts create custom-metrics-sd-adapter --project "$GCP_PROJECT_ID"

gcloud projects add-iam-policy-binding "$GCP_PROJECT_ID" \
  --member "serviceAccount:custom-metrics-sd-adapter@$GCP_PROJECT_ID.iam.gserviceaccount.com" \
  --role "roles/monitoring.editor"

gcloud iam service-accounts add-iam-policy-binding \
  --role roles/iam.workloadIdentityUser \
  --member "serviceAccount:$GCP_PROJECT_ID.svc.id.goog[custom-metrics/custom-metrics-stackdriver-adapter]" \
  "custom-metrics-sd-adapter@$GCP_PROJECT_ID.iam.gserviceaccount.com"

kubectl create -f https://raw.githubusercontent.com/GoogleCloudPlatform/k8s-stackdriver/master/custom-metrics-stackdriver-adapter/deploy/production/adapter.yaml

kubectl annotate serviceaccount custom-metrics-stackdriver-adapter \
  "iam.gke.io/gcp-service-account=custom-metrics-sd-adapter@$GCP_PROJECT_ID.iam.gserviceaccount.com" \
  --namespace custom-metrics

```
