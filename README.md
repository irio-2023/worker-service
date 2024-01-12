# WeWu worker service

## GKE config

```bash
# assuming proper service account is set up
gcloud container clusters get-credentials <cluster-name> --location <cluster-location>
kubectl apply -f https://raw.githubusercontent.com/GoogleCloudPlatform/k8s-stackdriver/master/custom-metrics-stackdriver-adapter/deploy/production/adapter_new_resource_model.yaml
```