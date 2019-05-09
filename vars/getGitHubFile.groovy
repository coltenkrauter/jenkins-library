import components.Config;

def call(raw_file_url) {

    try {
        def config = new Config();

        withCredentials([string(credentialsId: config.get("GITHUB_TOKEN_CRED_ID"), variable: 'TOKEN')]) {
            
            response = httpRequest (
                consoleLogResponseBody: false, 
                contentType: 'APPLICATION_JSON', 
                customHeaders: [[name: 'Authorization', value: "token ${TOKEN}"]],
                httpMode: 'GET', 
                url: "$raw_file_url", 
                validResponseCodes: '200'
            )

            URL url = new URL(raw_file_url);
            String fileName = FilenameUtils.getName(url.getPath());

            writeFile file: fileName, text: response.content

            sh 'ls -l'
            sh "cat ${fileName};"
        }
    } catch (err) {
        echo "Failed get_file_from_repo(): ${err}"
        throw err
    }
}
