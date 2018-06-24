
function combat(controllable,sense)
	controllable:Attack(player:getPosition().x,player:getPosition().y)
end

function civilian(controllable,sense,pos)
	player=sense:getPlayer(controllable,true)
	counter=controllable:getValue(0)
	xpos=controllable:getValue(1)
	ypos=controllable:getValue(2)
	if player:getPosition():getDistance(xpos,ypos)<8 then
		counter=counter+1
	else
		counter=counter-1
	end
	
	if counter>20 and sense:getPreference("unbirth")==true then
		if pos:getDistance(player:getPosition())<2 then
			controllable:startConversation()
			counter=0
		else	
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(player:getPosition().x,player:getPosition().y)
			end			
		end
	else
		if pos:getDistance(xpos,ypos)>2	then
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(xpos,ypos)
			end			
		end	
	end
	controllable:setValue(0,counter)
end

function setPosition(controllable,pos)
	xpos=controllable:getValue(1)
	respawnpos=controllable:getRespawnController():getStartPosition()
	if xpos==0 then	
		controllable:setValue(1,respawnpos.x)
		controllable:setValue(2,respawnpos.y)	
	end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	setPosition(controllable)

	if sense:getViolationLevel()>0 then
		combat(controllable,sense,pos)
	else
		civilian(controllable,sense)
	end
	
end  