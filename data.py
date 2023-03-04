from gps_class import GPSVis

vis = GPSVis(data_path = "coordinates.csv", map_path = "map.png",
             points = (51.75380, -0.24380, 51.74997, -0.23420))

vis.create_image(color = (0, 0, 0), width = 3)
vis.plot_map(output = "save")

print()