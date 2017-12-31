function main(worldManipulation)
	worldManipulation:setZone("crash site")
	worldManipulation:removeWidget(3,25)
	worldManipulation:removeWidget(9,30)	
	worldManipulation:RestoreShip("heavy",0,3)
end