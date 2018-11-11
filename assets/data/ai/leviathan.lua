
function main(controllable, sense, script)  
	player=sense:getPlayer(controllable,false)
	
	count=controllable:getValue(0);
	count=count+1
	if (count<100 and player:getPosition():getDistance(controllable:getPosition()<8) then
		count=100;
	end
	controllable:setValue(0,count);
	if (count==100) then
	sense:drawText("The Leviathan lets out a bellowing roar of such volume that it could be heard for miles")
	end
	if (count>100) then
		if (player:getPosition():getDistance(controllable:getPosition()<2)) then
			controllable:startConversation()	
		else
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(player:getPosition().x,player:getPosition().y,8)
			end	
		end
	end
end  