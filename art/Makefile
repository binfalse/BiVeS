all: logo dependencygraph


logo: logo.png logo-small.png logo-tiny.png logo.pdf

logo.png: logo.pdf
	convert -density 200 logo.pdf logo.png

logo-small.png: logo.pdf
	convert -density 50 logo.pdf logo-small.png

logo-tiny.png: logo.pdf
	convert -density 30 logo.pdf logo-tiny.png

logo.pdf: logo.tex
	pdflatex logo.tex


dependencygraph: dependency-graph.png

dependency-graph.png: dependency-graph.pdf
	convert -flatten dependency-graph.pdf dependency-graph.png

dependency-graph.pdf: dependency-graph.tex
	pdflatex dependency-graph.tex


clean:
	rm -rf logo.pdf logo-small.png logo.png logo.aux logo.log logo-tiny.png dependency-graph.png dependency-graph.pdf dependency-graph.log dependency-graph.aux
