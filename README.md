# javaSimpleModel

A simple example of an SEIRS epidemiological model using the [FAIR Data Pipeline](https://fairdatapipeline.github.io).

## Installation of dependencies

First, install Java (JDK v.11 or later), and gradle (v.6.5 or later) on your system. Then install the FAIR data registry if it is not already installed - see [here](https://fairdatapipeline.github.io/docs/local_registry/) for instructions. The [javaDataPipeline](https://github.com/FAIRDataPipeline/javaDataPipeline) API is already registered on [JitPack](https://jitpack.io), so you don't need to install it manually.

## Installing the command line tools

If you don't already have the registry initialised and the `fair` command line tool configured, then you need to install that next - see [here](https://github.com/FAIRDataPipeline/FAIR-CLI#installation) for more details. Briefly, with python and poetry (`pip install poetry`) installed:


```sh
git clone https://github.com/FAIRDataPipeline/FAIR-CLI.git
cd FAIR-CLI
poetry install
poetry shell
```

You are now running in a new shell created by `poetry` that has a copy of the `fair` executable in its path.

## Configuring the registry and running the example in this repo

Download and install the [javaSimpleModel](https://github.com/FAIRDataPipeline/javaSimpleModel) code. In some suitable directory, clone the git repo:

```sh
git clone https://github.com/FAIRDataPipeline/javaSimpleModel.git
cd javaSimpleModel
```

At this point you can configure `fair` to run in this repo. Either run:

```sh
fair init
```

And fill in your own details, or simply run:

```sh
fair init --ci
```

In this case the CLI will initialise the repo with dummy values that are used for continuous integration testing.

Finally you can run the example!

## Running the example

This is easy. The user configuration script for running the Java SEIRS model can be found inside this repo - [src/main/resources/seirs-config.yaml](https://github.com/FAIRDataPipeline/javaSimpleModel/blob/master/src/main/resources/seirs-config.yaml) - and for this self-contained example, it includes all of the information to register the input data that the model needs, so that you don't have to be connected to a registry that already knows about it. The code can be executed by first ensuring that all of the input data is available in the local registry (using `fair pull`) and then running the code (using `fair run`). So:

```sh
fair pull src/main/resources/seirs-config.yaml
fair run src/main/resources/seirs-config.yaml
```

That's it! If you go to the local registry in your browser now (by default at http://localhost:8000), you should see the input and output data recorded in the registry.