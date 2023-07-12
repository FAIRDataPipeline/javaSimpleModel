# javaSimpleModel
![build](https://github.com/FAIRDataPipeline/javaSimpleModel/actions/workflows/gradle-build.yml/badge.svg)
![run w fair cli](https://github.com/FAIRDataPipeline/javaSimpleModel/actions/workflows/runWithFairCli.yml/badge.svg)
[![License: GPL-3.0](https://img.shields.io/badge/licence-GPL--3-yellow)](https://opensource.org/licenses/GPL-3.0)
[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.5564237.svg)](https://doi.org/10.5281/zenodo.5564237)


A simple example of an SEIRS epidemiological model using the [FAIR Data Pipeline](http://fairdatapipeline.org).

## Installation of dependencies

First, install Java (JDK v.11 or later), and gradle (v.6.5 or later) on your system. Then install the FAIR data registry if it is not already installed - see [here](https://www.fairdatapipeline.org/docs/data_registry/installation/) for instructions. The [javaDataPipeline](https://github.com/FAIRDataPipeline/javaDataPipeline) API is registered on [Maven Central](https://repo1.maven.org/maven2/org/fairdatapipeline/api/), so gradle will automatically resolve it as a dependency.

## Installing the command line tools

If you don't already have the registry initialised and the `fair` command line tool configured, then you need to install that next - see [here](https://github.com/FAIRDataPipeline/FAIR-CLI#installation).

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

And fill in your own details.

Finally you can run the example!

## Running the example

This is easy. The user configuration script for running the Java SEIRS model can be found inside this repo - [src/main/resources/seirs-config.yaml](https://github.com/FAIRDataPipeline/javaSimpleModel/blob/master/src/main/resources/seirs-config.yaml) - and for this self-contained example, it includes all of the information to register the input data that the model needs, so that you don't have to be connected to a registry that already knows about it. The code can be executed by first ensuring that all of the input data is available in the local registry (using `fair pull`) and then running the code (using `fair run`). So:

```sh
fair pull src/main/resources/seirs-config.yaml
fair run src/main/resources/seirs-config.yaml
```

That's it! If you go to the local registry in your browser now (by default at http://localhost:8000), you should see the input and output data recorded in the registry.

## Other examples

In the [resources](https://github.com/FAIRDataPipeline/javaSimpleModel/blob/master/src/main/resources/) folder you will find two other example config files, one of which can create a new data product with parameters for the SEIRS model (in FAIR Data Pipeline TOML parameter file format), the other runs the same SEIRS model again using the TOML parameter Data Product.
