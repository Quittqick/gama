/**
* Name: Moving cells with neighbors
* Author: Arnaud Grignard
* Description: Third part of the tutorial : Tuto3D
* Tags: 3d, light, grid, neighbors
*/

model Tuto3D

global {
	int nb_cells <- 100;
	int environment_size <- 100;
	geometry shape <- cube(environment_size);

	init {
		create cell number: nb_cells {
			location <- {rnd(environment_size), rnd(environment_size), rnd(environment_size)};
		}
	}
}

species cell skills: [moving3D] {
	rgb color;
	list<cell> neighbors;
	int offset;

	reflex move {
		do wander;
	}

	reflex compute_neighbors {
		neighbors <- cell select ((each distance_to self) < 10);
	}

	aspect default {
		draw sphere(environment_size * 0.01) color: #orange;
		loop pp over: neighbors {
			draw line([self.location, pp.location]);
		}
	}
}

experiment Tuto3D type: gui {
	parameter "Initial number of cells: " var: nb_cells min: 1 max: 1000 category: "Cells";
	output {
		display View1 type: opengl background: rgb(10, 40, 55) {
			graphics "env" {
				draw cube(environment_size) color: #black wireframe: true;
			}
			species cell;
		}
	}
}
