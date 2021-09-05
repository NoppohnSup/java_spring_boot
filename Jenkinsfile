#!/usr/bin/env groovy
node ('master') {
    currentBuild.result = "SUCCESS"
    try {
        stage 'Checkout sourcecode'
        git url: 'https://github.com/NoppohnSup/java_spring_boot.git',
        poll: true,
        branch: git_branch

        stage 'Test'
        env.JAVA_HOME="/usr/java/jdk1.8.0_45"
        env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
        sh 'java -version'
        sh "/usr/bin/git rev-parse HEAD"
        sh "./gradlew clean build"

        stage 'Build docker image'
        sh "/usr/bin/git rev-parse HEAD"
        gitCommit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
        writeFile file: "version.txt", text: "{\"git_branch\": \"${env.GIT_BRANCH}\", \"git_commit\": \"$gitCommit\", \"version\": \"${project}:${base_version}.${env.BUILD_NUMBER}\"}"
        sh "./gradlew build buildDocker"

        stage 'Push docker image'
        docker.withRegistry("https://${repository}", "ecr:ap-southeast-1:cred") {
            docker.image(project).push("${base_version}.${env.BUILD_NUMBER}")
        }

        stage 'Deploy'
        println resource_file
        build job: 'ECS-Terraform-Deployment', parameters: [
            [$class: 'StringParameterValue', name: 'resource_file', value: resource_file],
            [$class: 'StringParameterValue', name: 'repository', value: repository],
            [$class: 'StringParameterValue', name: 'project', value: project],
            [$class: 'StringParameterValue', name: 'environment', value: environment],
            [$class: 'StringParameterValue', name: 'version', value: base_version + "." + env.BUILD_NUMBER]
        ]
    }
    catch (err) {

        currentBuild.result = "FAILURE"
        throw err
    }
}