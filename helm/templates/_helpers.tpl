{{/*
Expand the name of the chart.
*/}}
{{- define "mychart.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "mychart.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" | replace "." "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride | replace "." "-" }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" | replace "." "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" | replace "." "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "mychart.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "mychart.labels" -}}
helm.sh/chart: {{ include "mychart.chart" . }}
{{ include "mychart.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "mychart.selectorLabels" -}}
app.kubernetes.io/app: {{ include "mychart.name" . }}
{{- end }}

{{/*
Expand the image of the app.
*/}}
{{- define "mychart.image" -}}
{{- $imageName := .Values.imageName | default "myImageName" }}
{{- if .Values.imageRepo }}
{{- printf "%s/%s:%s" .Values.imageRepo $imageName .Chart.AppVersion | quote}}
{{- else }}
{{- printf "%s:%s" $imageName .Chart.AppVersion | quote}}
{{- end }}
{{- end }}

{{/*
merged environments as dict
*/}}
{{- define "mychart.envAsDict" -}}
{{- $defaultParamValues := .Default | default dict }}
{{- $selectedEnv := .Values.env | default "noenv" }}
{{- $selectedEnvValues := get .Values $selectedEnv | default dict }}
{{- $selectedEnvParamValues := get $selectedEnvValues .Param | default dict }}
{{- $defaultEnvParamValues := get .Values .Param | default dict }}
{{- mergeOverwrite $defaultParamValues $defaultEnvParamValues $selectedEnvParamValues | toYaml }}
{{- end }}

{{/*
merged environments as primitive
*/}}
{{- define "mychart.envAsPrimitive" -}}
{{- $selectedEnv := .Values.env | default "noenv" }}
{{- $selectedEnvValues := get .Values $selectedEnv | default dict }}
{{- get $selectedEnvValues .Param | default (get .Values .Param) }}
{{- end }}

{{/*
merged environments as boolean
*/}}
{{- define "mychart.envAsBool" -}}
{{- $selectedEnv := .Values.env | default "noenv" }}
{{- $selectedEnvValues := get .Values $selectedEnv | default dict }}
{{- $selectedEnvParamValue := get $selectedEnvValues .Param | quote }}
{{- $defaultEnvParamValue := get .Values .Param | quote }}
{{- $resultLen := 2 }}
{{- if ne (len $selectedEnvParamValue) 2 }}
{{- $resultLen = len $selectedEnvParamValue }}
{{- else }}
{{- $resultLen = len $defaultEnvParamValue }}
{{- end }}
{{- if eq $resultLen 2 }}
{{- .Default }}
{{- else if eq $resultLen 6 }}
{{- true }}
{{- else }}
{{- false }}
{{- end }}
{{- end }}

{{/*
merged config
*/}}
{{- define "mychart.configmap" -}}
{{- $templateParam := dict "Values" .Values "Param" "config" }}
{{- include "mychart.envAsDict" $templateParam }}
{{- end }}

{{/*
merged replicaCount
*/}}
{{- define "mychart.replicaCount" -}}
{{- $templateParam := dict "Values" .Values "Param" "replicaCount" }}
{{- include "mychart.envAsPrimitive" $templateParam | default 1 }}
{{- end }}

{{/*
merged resources
*/}}
{{- define "mychart.resources" -}}
{{- $defaultRequestsValue := dict "memory" "400Mi" "cpu" "50m" }}
{{- $defaultLimitsValue := dict "memory" "1500Mi" "cpu" "1000m" }}
{{- $defaultValue := dict "requests" $defaultRequestsValue "limits" $defaultLimitsValue }}
{{- $templateParam := dict "Values" .Values "Param" "resources" "Default" $defaultValue }}
{{- include "mychart.envAsDict" $templateParam }}
{{- end }}

{{/*
merged nodeSelector
*/}}
{{- define "mychart.nodeSelector" -}}
{{- $templateParam := dict "Values" .Values "Param" "nodeSelector" }}
{{- include "mychart.envAsDict" $templateParam }}
{{- end }}

{{/*
merged accessType
*/}}
{{- define "mychart.accessType" -}}
{{- $templateParam := dict "Values" .Values "Param" "accessType" }}
{{- include "mychart.envAsPrimitive" $templateParam | default "eds" }}
{{- end }}

{{/*
merged port
*/}}
{{- define "mychart.port" -}}
{{- $templateParam := dict "Values" .Values "Param" "port" }}
{{- include "mychart.envAsPrimitive" $templateParam | default 8080 }}
{{- end }}

{{/*
merged podAnnotations
*/}}
{{- define "mychart.podAnnotations" -}}
{{- $templateParam := dict "Values" .Values "Param" "podAnnotations" }}
{{- include "mychart.envAsDict" $templateParam }}
{{- end }}

{{/*
merged hasSecret
*/}}
{{- define "mychart.hasSecret" -}}
{{- $templateParam := dict "Values" .Values "Param" "hasSecret" "Default" true }}
{{- include "mychart.envAsBool" $templateParam }}
{{- end }}