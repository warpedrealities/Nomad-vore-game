
function combat(controllable,sense,pos,hostile,player)

	if pos:getDistance(hostile:getPosition())<2 then
	controllable:setAttack(0)
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
	else 
		if player:getDistance(hostile:getPosition())<2 then
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,2)
				controllable:FollowPath()
			end		
		end
	end
end

function medical(controllable,player,sense,pos)
	if (player:getPosition():getDistance(pos)<2) then
		sense:drawText("operator:administering emergency medical aid")
		controllable:setAttack(1)
		controllable:Attack(player:getPosition().x,player:getPosition().y)
		controllable:setValue(0,1)
		sense:drawText("operator: medigel reserves depleted")
	else
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:Pathto(player:getPosition().x,player:getPosition().y,2)
			controllable:FollowPath()
		end	
	
	end
end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	player=sense:getPlayer(controllable,false)
	if (player:getRPG():getStat(0)<15) and (controllable:getValue(0)==0)then
				medical(controllable,player,sense,pos)
	else
		hostile=sense:getHostile(controllable,10,true)
		if not (hostile == nil ) then
			--combat ai here
			combat(controllable,sense,pos,hostile,player)
		else
			if controllable:HasPath() then
			controllable:FollowPath()
			else
				if (player:getPosition():getDistance(pos)>4) then
					if controllable:HasPath() then
					controllable:FollowPath()
					else
					controllable:Pathto(player:getPosition().x,player:getPosition().y,8)
					controllable:FollowPath()
					end		
				else
					a=math.random(0,8)
					controllable:move(a);
				end		
			end	
		end	
	end
	
end  