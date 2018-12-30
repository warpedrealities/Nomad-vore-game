
function attack(controllable,sense, hostile)
	--approach, attack, if the player is below 50 satiation do not retreat afterwards
	if pos:getDistance(hostile:getPosition())<2 then
		controllable:getRPG():removeStatus(23)
		--if in melee range attack
		if (hostile:getRPG():getStat(2)<50) then

			controllable:setAttack(0)		
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)	
		else

			controllable:setAttack(1)		
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
			if hostile:getRPG():hasStatus(22) then	
				print("lose attack mode")
				controllable:setValue(0,0)
				controllable:setValue(1,0)	
			end	
		end

	else
	--if not move towards player
		if controllable:HasPath() then
		controllable:FollowPath()
		else
		controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
		end
	end

end

function stealth(controllable,sense)
	if controllable:HasPath() then
		controllable:FollowPath()
	else
		--stealth if not unstealthed
		if (controllable:getRPG():hasStatus(23)==false) then

			controllable:useSelfMove(2)
		else 
			--heal if injured
			health=controllable:getRPG():getStat(0);
			resolve=controllable:getRPG():getStat(1);	
			if (resolve==75) and (health==75) then
			--if perfectly fine roll a dice for if to engage attack mode		
				dice=math.random(20)
				print("dice", dice)
				if (dice==20) then
					controllable:setValue(0,1)
				else
					a=math.random(0,8)
					controllable:move(a);	
				end
			else

				controllable:useSelfMove(2)	
			end			
		end				
	end
end

function rage(controllable,sense,hostile)
	if pos:getDistance(hostile:getPosition())<2 then
		--if in melee range attack

		controllable:getRPG():removeStatus(23)
		controllable:setAttack(0)			
		controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)

	else
		--if not move towards player
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y)
				controllable:FollowPath()	
		end
	end
end

function flee(controllable,sense,hostile)
--start counting up to going mental and attacking the player without mercy
	psycho=controllable:getValue(1)
	if (psycho>19) then

		rage(controllable,sense,hostile)
	else
		--otherwise retreat	
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:specialCommand("hide")
		end
		controllable:setValue(1,psycho+1)
	end

end

function main(controllable, sense, script)  
	pos=controllable:getPosition()
	hostile=sense:getPlayer(controllable,false)
	if (controllable:getValue(0)==1) then
		attack(controllable,sense,hostile)
	else
		if (sense:getTileVisible(pos.x,pos.y)) and (controllable:getRPG():hasStatus(23)==false) then
			flee(controllable,sense,hostile)
		else
			stealth(controllable,sense,hostile)		
		end
	end
end 
