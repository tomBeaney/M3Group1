class movement:

    global i
    i = 0

    def getLastLine():
        global coordinates, splitCoords, latitude, longitude, newLatitude, newLongitude
        file = open("coordinates1.csv", "r")
        with file as f:
            for line in f:
                pass
                coordinates = line
                splitCoord = line.split()
                splitCoords = " ".join(line.split())
                latitude = splitCoord[0] 
                longitude = splitCoord[2]
            file.close()
            #print(splitCoords)
            print(coordinates)
            #print(latitude)
            #print(longitude)
        return longitude, latitude

    def areWeThereYet():
        #latitude = splitCoords[0]
        #longitude = splitCoords[2]
        goalDestination = ["51.750652 , -0.240625"]
        if splitCoords == goalDestination[0]:
            print("Yes")

    def navigation():
        tempLongitude = longitude
        newLongitude = tempLongitude
        tempLatitude = latitude
        newLatitude = tempLatitude
        
        if float(longitude) == float(newLongitude):
            print("equal")
        
        elif float(longitude) > float(newLongitude):
            print("forward")
            
        elif float(longitude) < float(newLongitude):
            print("reverse")
            
        elif float(latitude) > float(newLatitude):
            print("left")
            
        elif float(latitude) < float(newLatitude):
            print("right")
        
    def convertToHeadings(longitude, latitude):
        if float(longitude) > 0.0 and float(longitude) < 90.0:
            print("North")   
        if float(longitude) < 0.0 and float(longitude) > -90.0:
            print("South")
        if float(latitude) > 0.0 and float(latitude) < 90.0:
            print("West")
        if float(latitude) < 0.0 and float(latitude) > -90.0:
            print("East")

    while i < 5:
        i += 1
        getLastLine()
        areWeThereYet()
        navigation()
        convertToHeadings(longitude, latitude)

movement()
