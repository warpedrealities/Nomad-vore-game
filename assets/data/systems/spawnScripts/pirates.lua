
function main(tools)  
	a=math.random(0,8)
	if (tools:getFactions():getFaction("pirate"):getFactionFlags():readFlag("encounter")==0) then
		tools:getFactions():getFaction("pirate"):getFactionFlags():setFlag("encounter",1)
		--spawn pirate
		tools:spawn("sloop","pirateGalleon")
	else
		if (a<2) then
			if (tools:countShips("sloop")<3) then
				tools:spawn("sloop","pirateGalleon")
			end
		else 
			if (a>7) then
				if (tools:countShips("sloop")>0) then
					tools:deSpawn("sloop")
				end	
			end
		end
	end  
end
