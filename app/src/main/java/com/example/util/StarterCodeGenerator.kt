package com.example.util

import com.example.model.AuthType
import com.example.model.PublicApi
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object StarterCodeGenerator {

    fun generateSnippet(api: PublicApi, language: String): String {
        val endpoint = api.testEndpoint ?: "https://api.example.com/v1/data"
        val authHeaderJs = if (api.auth == AuthType.API_KEY) "  headers: { 'Authorization': 'Bearer YOUR_API_KEY' },\n" else ""
        val authHeaderPy = if (api.auth == AuthType.API_KEY) "headers = {'Authorization': 'Bearer YOUR_API_KEY'}\n" else ""
        val authHeaderDart = if (api.auth == AuthType.API_KEY) "  headers: {'Authorization': 'Bearer YOUR_API_KEY'},\n" else ""

        return when (language.lowercase()) {
            "javascript", "js" -> {
                """// ${api.name} - Rapid Integration (JavaScript / Node.js)
async function fetch${api.name.replace("[^A-Za-z0-9]".toRegex(), "")}() {
  try {
    const response = await fetch("${endpoint}", {
      method: 'GET',
${authHeaderJs}    });
    if (!response.ok) throw new Error(`HTTP error! status: ${'$'}{response.status}`);
    const data = await response.json();
    console.log("Success:", data);
    return data;
  } catch (error) {
    console.error("API Error:", error);
  }
}

fetch${api.name.replace("[^A-Za-z0-9]".toRegex(), "")}();""".trimIndent()
            }
            "python", "py" -> {
                """# ${api.name} - Rapid Integration (Python 3)
import requests

def get_${api.name.lowercase().replace("[^a-z0-9]".toRegex(), "_")}_data():
    url = "${endpoint}"
    ${if (authHeaderPy.isNotEmpty()) authHeaderPy else "# No auth required"}
    try:
        response = requests.get(url${if (authHeaderPy.isNotEmpty()) ", headers=headers" else ""}, timeout=10)
        response.raise_for_status()
        data = response.json()
        print("Received payload:", data)
        return data
    except requests.RequestException as e:
        print(f"Network error: {e}")
        return None

if __name__ == "__main__":
    get_${api.name.lowercase().replace("[^a-z0-9]".toRegex(), "_")}_data()""".trimIndent()
            }
            "dart" -> {
                """// ${api.name} - Rapid Integration (Dart / Flutter)
import 'dart:convert';
import 'package:http/http.dart' as http;

Future<void> fetch${api.name.replace("[^A-Za-z0-9]".toRegex(), "")}() async {
  final uri = Uri.parse('${endpoint}');
  try {
    final response = await http.get(
      uri,
${authHeaderDart}    );
    if (response.statusCode == 200) {
      final jsonResponse = jsonDecode(response.body);
      print('Response: ${'$'}jsonResponse');
    } else {
      print('Failed with status: ${'$'}{response.statusCode}');
    }
  } catch (e) {
    print('Exception: ${'$'}e');
  }
}

void main() => fetch${api.name.replace("[^A-Za-z0-9]".toRegex(), "")}();""".trimIndent()
            }
            else -> api.sampleJs.ifEmpty { "console.log('API: ${api.name}');" }
        }
    }

    fun generateFullProjectScaffold(api: PublicApi, language: String): String {
        return when (language.lowercase()) {
            "javascript", "js" -> """
📦 Project: ${api.name.lowercase().replace(" ", "-")}-starter
├── package.json
├── .env.example
├── src/
│   ├── index.js
│   ├── client.js
│   └── types.d.ts
└── README.md

--- package.json ---
{
  "name": "${api.name.lowercase().replace(" ", "-")}-client",
  "version": "1.0.0",
  "type": "module",
  "scripts": { "start": "node src/index.js", "dev": "nodemon src/index.js" },
  "dependencies": { "dotenv": "^16.4.5" }
}

--- src/client.js ---
import 'dotenv/config';

export class ${api.name.replace("[^A-Za-z0-9]".toRegex(), "")}Client {
  constructor(apiKey = process.env.API_KEY) {
    this.apiKey = apiKey;
    this.baseUrl = "${api.testEndpoint ?: "https://api.example.com"}";
  }

  async query(endpoint = "", params = {}) {
    const url = new URL(endpoint, this.baseUrl);
    Object.entries(params).forEach(([k, v]) => url.searchParams.append(k, v));
    const res = await fetch(url, {
      headers: {
        'Accept': 'application/json',
        ...(this.apiKey && { 'Authorization': `Bearer ${'$'}{this.apiKey}` })
      }
    });
    if (!res.ok) throw new Error(`Status ${'$'}{res.status}: ${'$'}{res.statusText}`);
    return res.json();
  }
}
            """.trimIndent()

            "python", "py" -> """
📦 Project: ${api.name.lowercase().replace(" ", "-")}-py
├── requirements.txt
├── .env.example
├── app/
│   ├── __init__.py
│   ├── main.py
│   └── client.py
└── README.md

--- requirements.txt ---
requests>=2.31.0
python-dotenv>=1.0.0
pydantic>=2.0.0

--- app/client.py ---
import os
import requests
from dotenv import load_dotenv

load_dotenv()

class ${api.name.replace("[^A-Za-z0-9]".toRegex(), "")}Client:
    def __init__(self, api_key: str = None):
        self.api_key = api_key or os.getenv("API_KEY")
        self.base_url = "${api.testEndpoint ?: "https://api.example.com"}"
        self.session = requests.Session()

    def get_data(self, params: dict = None):
        headers = {"Accept": "application/json"}
        if self.api_key:
            headers["Authorization"] = f"Bearer {self.api_key}"
        res = self.session.get(self.base_url, headers=headers, params=params, timeout=15)
        res.raise_for_status()
        return res.json()
            """.trimIndent()

            else -> """
📦 Project: ${api.name.lowercase().replace(" ", "-")}-dart
├── pubspec.yaml
├── lib/
│   ├── client.dart
│   └── main.dart
└── README.md

--- pubspec.yaml ---
name: ${api.name.lowercase().replace(" ", "_")}_app
description: Starter client for ${api.name}
environment:
  sdk: '>=3.0.0 <4.0.0'
dependencies:
  http: ^1.2.0
            """.trimIndent()
        }
    }

    fun buildZipScaffold(api: PublicApi, language: String): ByteArray {
        val bos = ByteArrayOutputStream()
        ZipOutputStream(bos).use { zos ->
            val readme = "# ${api.name} Starter Project\nGenerated by APIMatch (https://apimatch.dev)\nDocs: ${api.docsUrl}\n"
            zos.putNextEntry(ZipEntry("README.md"))
            zos.write(readme.toByteArray())
            zos.closeEntry()

            val scaffold = generateFullProjectScaffold(api, language)
            zos.putNextEntry(ZipEntry("scaffold-instructions.txt"))
            zos.write(scaffold.toByteArray())
            zos.closeEntry()

            val snippet = generateSnippet(api, language)
            val codeFile = when (language.lowercase()) {
                "python", "py" -> "client.py"
                "dart" -> "client.dart"
                else -> "client.js"
            }
            zos.putNextEntry(ZipEntry(codeFile))
            zos.write(snippet.toByteArray())
            zos.closeEntry()
        }
        return bos.toByteArray()
    }
}
