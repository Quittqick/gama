/**
* Name: MapQuestImageImport
* Author: Alexis Drogoul
* Description: Demonstrates how to load a (possibly dynamic) image from MapQuest https://developer.mapquest.com/documentation/samples/static-map/v4/get-map/ and how to refresh it
* Tags: data_loading, displays, user_input, on_change
*/
model MapQuestImageImport

 
global
{
	image_file static_map_request;
	map
	answers <- user_input_dialog("Address can be a pair lat,lon (e.g; '48.8566140,2.3522219')", [enter("Address","48.8566140,2.3522219")]);
	string center_text <- answers["Address"]; 
	int zoom_text <- 10;
	action load_map
	{ 
		string appkey<-"G1VfvZoE8T4g0U24bm7UbuozvS9YIGXt";
		string zoom <- "zoom=" + zoom_text;
		string center <- "center=" + center_text;
		static_map_request <-
		image_file("https://www.mapquestapi.com/staticmap/v4/getmap?key="+appkey+"&size=600,600&type=map&imagetype=jpg&"+zoom+"&scalebar=false&traffic=false&"+center+"");
	}
 
	init
	{
		do load_map;
	}

}

experiment Display
{
	
	parameter "Zoom" var: zoom_text  {
		ask simulation  {do load_map;}
		do update_outputs(true);
	}
	 
	output
	{
		display "Google Map" type: opengl
		{
			image static_map_request;
		}

	}

}

