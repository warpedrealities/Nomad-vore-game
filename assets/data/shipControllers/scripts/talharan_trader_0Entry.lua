function checkFTL(script,sense)
	player=sense:getPlayer()
	ftl=player:getShipStats():getFTL()
	if (ftl>0) then
		script:removeShip()
	end

end

function moveAway(script,sense)
	local pos=script:getPosition()
	if (pos.x<200)
	{
		script:reposition(pos.x+1000,pos.y+1000)
	}
end

function moveBack(script,sense)
	script:getFlags():setFlag("boarding",0)
	local pos=script:getPosition()
	if (pos.x>200)
	{
		script:reposition(pos.x-1000,pos.y-1000)
	}

end

function checkBoarding(script,sense)
	boarding=script:getFlags():readFlag("boarding")
	if (boarding>0) then
		markTime=script:getFlags():readFlag("CLOCK")
		actualTime=marktTime*100
		worldTime=sense:getTime();
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
	boarded=script:getFlags():readFlag("boarded")
	if (boarding>0)then
		script:removeShip()
	end

end

function main(script,sense)  
	checkBoarding(script,sense)
	checkFTL(script,sense)
	checkBoarded(script,sense)

end  