
function melee(controllable,sense,pos,hostile)
	if pos:getDistance(hostile:getPosition())<2 then
		--if in melee range attack
		if (controllable:getValue(0)==3) then
			controllable:setValue(0,0)		
			controllable:setAttack(2)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)			
		else
			controllable:setAttack(0)
			result = controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			if not (result==nil) then
				if (result:getValue()==1) then
					controllable:setValue(0,0)	
				else
					controllable:setValue(0,controllable:getValue(0)+1)
				end
			end	
		end
		
	else
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,3)
		end
	end		
end

function jump(controllable,sense,pos,hostile)

	b=controllable:getValue(0)
	if (b==0) and pos:getDistance(hostile:getPosition())>3 then
	controllable:setAttack(1);
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
	b=1
	controllable:setValue(0,b)	
	else
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
		end	
	end
	
end

function combat(controllable,sense,pos,hostile)

	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
		melee(controllable,sense,pos,hostile)
	else
	--if not move towards player
		jump(controllable,sense,pos,hostile)
	end

end

function main(controllable,sense,script)
	pos=controllable:getPosition()
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) then
		if not controllable:isPeace() then
			--combat ai here
			combat(controllable,sense,pos,hostile)
		else
			if (controllable:isPeace()) and pos:getDistance(hostile:getPosition())<6 and (controllable:getFlag("talked")==0) then
				controllable:startConversation()
			end
		end
	end
end
