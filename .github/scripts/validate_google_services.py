#!/usr/bin/env python3
import json
import sys
import os

PATH = 'app/google-services.json'
if not os.path.isfile(PATH):
    print('ERROR: app/google-services.json not found', file=sys.stderr)
    sys.exit(1)

try:
    with open(PATH, 'r', encoding='utf-8') as f:
        data = json.load(f)
except Exception as e:
    print('ERROR: invalid JSON:', e, file=sys.stderr)
    sys.exit(2)

if not (isinstance(data, dict) and 'project_info' in data and 'client' in data):
    print('ERROR: missing required top-level keys: project_info and/or client', file=sys.stderr)
    sys.exit(3)

print('google-services.json parsed OK (project_info & client present)')
sys.exit(0)

