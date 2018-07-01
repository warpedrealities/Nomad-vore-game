
function main(tools)  
	a=math.random(0,8)
	if (tools:getFactions():getFaction("pirate"):getFactionFlags():readFlag("encounter")==0) then
		tools:getFactions():getFaction("pirate"):getFactionFlags():setFlag("encounter",1)
		--spawn pirate
		tools:spawn("sloop","pirateSloop")
	else
		if (a<2) then
			if (tools:countShips("sloop")<3) then
				tools:spawn("sloop","pirateSloop")
			end
		else 
			
		end
	end  
end
