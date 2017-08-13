function checkFTL(script,sense)
	player=sense:getPlayer()
	local ftl=player:getShipStats():getFTL()
	if (ftl>0) then
		local playerFTL=sense:getFlags():readFlag("playerFTL")
		if (playerFTL==0) then
			local worldTime0=sense:getTime()/100;
			sense:getFlags():setFlag("playerFTLTime",worldTime0)
		else
			local markTime0=sense:getFlags():readFlag("playerFTLTime")
			local actualTime0=marktTime0*100
			local worldTime0=sense:getTime();
			if (worldTime0-actualTime0<2000) then
				script:removeShip()
			end
		end

	end

end

function moveAway(script,sense)
	local pos=script:getPosition()
	if (pos.x<200) then

		script:reposition(pos.x+1000,pos.y+1000)
	end
end

function moveBack(script,sense)
	sense:getFlags():setFlag("boarding",0)
	local pos=script:getPosition()
	if (pos.x>200) then

		script:reposition(pos.x-1000,pos.y-1000)
	end

end

function checkBoarding(script,sense)
	boarding=sense:getFlags():readFlag("boarding")
	if (boarding>0) then
		local markTime=sense:getFlags():readFlag("CLOCK")
		local actualTime=marktTime*100
		local worldTime=sense:getTime();
		if (worldTime-actualTime<1000) then
			--move away
			moveAway(script,sense)
		else
			--move back
			moveBack(script,sense)
		end
	end

end

function checkBoarded(script,sense)
	boarded=sense:getFlags():readFlag("boarded")
	if (boarded>0)then
		script:removeShip()
	end

end

function main(script,sense)  
	print("running entry script")
	checkBoarding(script,sense)
	checkFTL(script,sense)
	checkBoarded(script,sense)

end  