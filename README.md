# BiVeS Framework

![bives logo](https://sems.uni-rostock.de/wp-content/uploads/2012/12/logo-icon-16.png) BiVeS is a library based on the XyDiff algorithm. It is able to detect differences between two versions of a computational model and to communicate these changes.

The Algorithm implemented in BiVeS was published as
> Martin Scharm, Olaf Wolkenhauer, Dagmar Waltemath:
> [An algorithm to detect and communicate the differences in computational models describing biological systems.](https://doi.org/10.1093/bioinformatics/btv484)
> *Bioinformatics* 32 (4): 563-570;

## Usage

BiVeS currently supports different flavours of XML documents.
It supports special comparison strategies for computational models encoded in SBML (though [BiVeS-SBML](https://github.com/binfalse/BiVeS-SBML)) or CellML (through [BiVeS-CellML](https://github.com/binfalse/BiVeS-CellML)).

To just compare two documents `A.xml` and `B.xml` just call it as:

    java -jar BiVeS.jar A.xml B.xml

The output will be an XML patch listing all operations necessary to convert `A.xml` into `B.xml`.
An XSD schema for the XML can be found in [res/bives-diff-schema.xsd](res/bives-diff-schema.xsd).

BiVeS supports other output formats and flags to activate special merge strategies, just call it with `--help` to get a full list of available options.

There is also a Web Application that happily accepts thrown models and returns comparison results.
Just have a look at [BiVeS-WS](https://github.com/SemsProject/BiVeS-WS) and its demo instance at [bives.bio.informatik.uni-rostock.de](https://bives.bio.informatik.uni-rostock.de/).
