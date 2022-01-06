# Overview of Submission

Project Structure

* The structure of my submission is similar to the original starter code.  

Endpoints

* `/instance-kinds` Queries the SmartCloud `/instances` endpoint and returns the list of available instances, in a slightly different format.
    ```
    GET 0.0.0.0:8080/instance-kinds
    [
        { "kind": "sc2-micro" },
        { "kind": "sc2-small" },
        ...
        { "kind": "sc2-hicpu-32" }
    ]
    ```

* `/prices?kind=<kind>` Queries the SmartCloud API for pricing information about the specified instance.
    ```
    GET 0.0.0.0:8080/prices?kind=sc2-small
    {
        "kind": "sc2-small",
        "amount": 0.471
    }
    ```

Features

* When the SmartCloud API returns a `500 Internal Server Error`, the http client uses an exponential backoff retry policy to attempt to return a result.
* When the SmartCloud API returns a `529 Too Many Requests`  

Things that could be improved with more time:

* Test coverage is not 100%.  For instance, I don't have any tests checking that `SmartServer` uses the configuration files correctly.  I also don't have tests for the `RetryPolicy`.
* Since the Docker container which serves the SmartCloud API is rate-limited and prone to server failures, all of the tests currently use a `MockClientService` to test the behavior of the routes I implemented.  Also, the tests only test the `SmartApp` implementation, and I don't include any tests for `SmartServer`, which runs the actual server.  In a production setting, it would also be important to directly test the interaction between these local/remote servers.  This would probably involve mocking the SmartCloud API so that it never fails (or, so that it fails in a controlled way) and running automated tests in a docker container.
* This is my first time writing `Scala` (as well as `cats`, `cats-effect`, `http4s`).  With more time/experience, I'd become more familiar with Scala idioms and best practices.

Other possible improvements:

* The starter code contained an `InstanceKindsService` corresponding to the `/instance-kinds` endpoint, suggesting that a `/prices` endpoint should have a corresponding `PriceService`, and so on.  However, since these are the only two endpoints, and both simply "wrap" calls to an HTTP client, I implemented both in a single `ClientService`, which abstracts away all API calls to the SmartCloud instance.  For a larger application with more complex behavior, it might make sense to separate requests and response handling into a number of different `XYZService`s.

# SmartCloud Project Instructions

**Important: Do NOT fork this repository if you want to submit a solution.**

Imagine we run our infrastructure on a fictional cloud provider, Smartcloud. As their machine instance prices fluctuate all the time, Smartcloud provides an API for us to retrieve their prices in real time. This helps us in managing our cost.

# Requirements

Implement an API for fetching and returning machine instance prices from Smartcloud.

```
GET /prices?kind=sc2-micro
{"kind":"sc2-micro","amount":0.42}, ... (omitted)
```

This project scaffold provides an end-to-end implementation of an API endpoint which returns some dummy data. You should try to follow the same code structure.

You should implement `SmartcloudPriceService` to call the [smartcloud](https://hub.docker.com/r/smartpayco/smartcloud) endpoint and return the price data. Note that the smartcloud service has usage quota and may return error occassionally to simulate unexpected errors. Please make sure your service is able to handle the constraint and errors gracefully.

You should also include a README file to document:-
1. Any assumptions you make
1. Any design decisions you make
1. Instruction on how to run your code

You should use git and make small commits with meaningful commit messages as you implement your solution.

# Setup

Follow the instruction at [smartcloud](https://hub.docker.com/r/smartpayco/smartcloud) to run the Docker container on your machine.

Clone or download this project onto your machine and run

```
$ sbt run
```

The API should be running on your port 8080.

# How to submit

Please push your code to a public repository and submit the link via email. Please do not fork this repository.
