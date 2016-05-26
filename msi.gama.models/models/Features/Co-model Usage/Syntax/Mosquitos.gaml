model Mosquitos


global
{
	geometry shape<-square(100);
	file icon<-file("../img/mosquito.png");
	int n <- 50;
	init
	{
		create Mosquito number: n;
	}

}

species Mosquito skills: [moving]
{
	geometry shape<-circle(1);
	int durability<- rnd(100);
	reflex dolive
	{	
		do wander amplitude:rnd(30) speed:0.5;		
	}

	aspect default
	{
		draw icon size:4 color: # green rotate:heading+180;
	}

}

experiment Generic type: gui
{ 
	output
	{
		display "Mosquitos display"
		{
			species Mosquito aspect: default;
		}

	}

}


