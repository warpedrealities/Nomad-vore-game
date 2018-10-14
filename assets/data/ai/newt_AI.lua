
function flee(controllable,sense, script)
	if controllable:HasPath() then
		controllable:FollowPath()
	else
		controllable:specialCommand("flee")
	end
	hostile=sense:getHostile(controllable,10,true)
	if not ((hostile==nil) and controllable:getRPG():hasStatus(23)) then
		controllable:useSelfMove(5)		
	end	
end

function stealthattack(pos,controllable,sense,script)
	hostile=sense:getHostile(controllable,32,false)
	if not (hostile==nil) then
		if (pos:getDistance(hostile:getPosition())<4) then
			controllable:getRPG():removeStatus(23)
			controllable:setAttack(2)	
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
				if (pos:getDistance(hostile:getPosition())<2) then
					controllable:setAttack(3)	
					controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
				end
			else
				if controllable:HasPath() then
					controllable:FollowPath()
				else
					controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,8)
				end
			end
	end
end

function stealth(pos,controllable,sense, script)
	health=controllable:getRPG():getStat(0);
	resolve=controllable:getRPG():getStat(1);
	if (health==50) and (resolve==50) then
		stealthattack(pos,controllable,sense, script)
	else
		controllable:useSelfMove(6)			
	end	
end

function notstealthed(pos,controllable,sense,script)
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile==nil) then	
		health=controllable:getRPG():getStat(0);
		resolve=controllable:getRPG():getStat(1);
		if (health<25) or (resolve<25) then		
			controllable:setAttack(4)	
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
			controllable:setValue(0,40)			
		else
			if (pos:getDistance(hostile:getPosition())<2) then
				if (hostile:getRPG():hasStatus(20)) then
					controllable:setAttack(0)	
					controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
				else
					controllable:setAttack(1)	
					controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
				end
			else
				if controllable:HasPath() then
					controllable:FollowPath()
				else
					controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
				end	
			end
		end
	else
		controllable:useSelfMove(5)		
	end
end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	fleeCounter=controllable:getValue(0)
	if (fleeCounter>0) then
		fleeCounter=fleeCounter-1
		controllable:setValue(0,fleeCounter)		
	else
		if (controllable:getRPG():hasStatus(23)) then
			stealth(pos,controllable,sense, script)
		else
			notstealthed(pos,controllable,sense,script)
		end
	end

end  
