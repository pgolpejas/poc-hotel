#!/bin/bash

# Specify the folder containing the Avro schema files
SCHEMA_REGISTRY_URL="http://schema-registry-it:8081"
SCHEMA_FOLDER="v1/imports"
ENVELOPE_FOLDER="v1/envelope"

_log() {
  echo "[ENTRYPOINT] $1"
}

_debug() {
  if [ "$ENTRYPOINT_DEBUG" = "true" ]; then
    echo "[ENTRYPOINT_DEBUG] $1"
  fi
}

register_schemas() {
    # Check if the specified folder exists
    if [ ! -d "$SCHEMA_FOLDER" ]; then
        echo "Error: Folder '$SCHEMA_FOLDER' does not exist."
        exit 1
    fi

    # Find all .avsc files in the folder and iterate over them
    find "$SCHEMA_FOLDER" -name "*.avsc" -type f | while IFS= read -r schema_file; do
        _log "Registering schema $schema_file"

        SCHEMA=$(cat "${schema_file}")
        # _log "Schema is $SCHEMA"

        SUBJECT_NAMESPACE=$(echo "$SCHEMA" | jq -r '.namespace')
        SUBJECT_NAME=$(echo "$SCHEMA" | jq -r '.name')
        SUBJECT="$SUBJECT_NAMESPACE.${SUBJECT_NAME}"
        # _log "Subject is $SUBJECT"

        url="$SCHEMA_REGISTRY_URL/subjects/${SUBJECT}/versions"
        _log "$url"

        SCHEMA_JSON=$(echo '{"schema":""}' | jq --arg schema "$SCHEMA" '.schema = $schema')
        # _log "SCHEMA_JSON is $SCHEMA_JSON"

        CURL=$(echo "curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" --data "$SCHEMA_JSON" "${url}"")
        # _log "Request is $CURL"

        curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
        --data "$SCHEMA_JSON" \
        "${url}"

        echo -e "\n--------------------------------------"
    done

}

parse_envelope_to_array(){
    local envelope_file="$1"
    local -n arr_lines="$2"

    # Read each line from the file and parse into an array of strings
    while IFS= read -r line; do
        # _log "Line before is $line"
        # Remove leading and trailing whitespace from the line
        line=$(echo "$line" | sed 's/^[[:space:]]*//' | sed 's/[[:space:]]*$//')
        # _log "Line after is $line"

        # Remove square brackets and quotes from the line
        line=$(echo "$line" | sed 's/\[//' | sed 's/\]//' | sed 's/\"//g')
        line="${line//,/}"
        
        # Split the line into an array of strings
        if [[ -n $line ]]; then
            arr_lines+=("$line")
        fi

    done < "$envelope_file"
}

register_envelopes() {
    # Check if the specified folder exists
    if [ ! -d "$ENVELOPE_FOLDER" ]; then
        echo "Error: Folder '$ENVELOPE_FOLDER' does not exist."
        exit 1
    fi

    template_envelope='{"subject":"", "references": [], "schema":""}'
    template_envelope_reference='{"name":"", "subject": [], "version":1}'

    # Find all .avsc files in the folder and iterate over them
    find "$ENVELOPE_FOLDER" -name "*.avsc" -type f | while IFS= read -r envelope_file; do
        _log "Registering envelope $envelope_file"

        ENVELOPE=$(cat "${envelope_file}")
        _log "Envelope is $ENVELOPE"

        # Extract the filename without extension
        filename=$(basename -- "$envelope_file")
        filename_no_ext="${filename%.*}"

        # Output the filename without extension
        _log "Filename without extension: $filename_no_ext" 
        SUBJECT="$filename_no_ext-value"

        arr_subjects=()
        parse_envelope_to_array "${envelope_file}" arr_subjects

        arr_references=()
        for subject in "${arr_subjects[@]}"; do
            # _log "Item subject: $subject"
            reference=$(echo $template_envelope_reference | jq --arg name "$subject" '.name = $name')
            reference=$(echo $reference | jq --arg subject "$subject" '.subject = $subject')
            arr_references+=("$reference")
            # _log "reference parsed: $reference"
        done

        # for reference_parsed in "${arr_references[@]}"; do
        #     _log "reference parsed: $reference_parsed"
        # done

        

        json_array=$(IFS=,; echo "[${arr_references[*]}]")

        # Merge the JSON array into the template JSON using jq
        SCHEMA_JSON=$(jq --argjson array "$json_array" '.references = $array' <<< "$template_envelope")
        SCHEMA_JSON=$(echo $SCHEMA_JSON | jq --arg subject "$SUBJECT" '.subject = $subject')
        SCHEMA_JSON=$(echo $SCHEMA_JSON | jq --arg schema "$ENVELOPE" '.schema = $schema')

        # Output the resulting JSON
        # _log "envelope json $SCHEMA_JSON"

        url="$SCHEMA_REGISTRY_URL/subjects/${SUBJECT}/versions"
        _log "$url"

        CURL=$(echo "curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" --data "$SCHEMA_JSON" "${url}"")
        # _log "Request is $CURL"

        curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
        --data "$SCHEMA_JSON" \
        "${url}"

        echo -e "\n--------------------------------------"
    done

}

main(){
  # Wait for Schema Registry to be ready
  while [[ "$(curl -s -o /dev/null -w "%{http_code}" $SCHEMA_REGISTRY_URL)" != "200" ]]; do
    _log "Waiting for Schema Registry to be ready..."
    sleep 5;
  done

  _log "Schema Registry initialized."

  register_schemas

  register_envelopes

  _log "Schemas imported successfully."
}

main
