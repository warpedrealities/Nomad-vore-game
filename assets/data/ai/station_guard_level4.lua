
function combat(controllable,sense,script,hostile)
	pos=controllable:getPosition()
	if pos:getDistance(hostile:getPosition())<2 then
	--if in melee range attack
	controllable:setAttack(0)
	controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)

	else
		
		a=math.random(0,2)
		if (a>0) then
			controllable:setAttack(1)
			controllable:Attack(hostile:getPosition().x,hostile:getPosition().y)
		else
		--if not move towards player
			if controllable:HasPath() then
				controllable:FollowPath()
			else
				controllable:Pathto(hostile:getPosition().x,hostile:getPosition().y,1)
			end
		end
	end
end

function pursue(controllable,sense,script)
		if controllable:HasPath() then
			controllable:FollowPath()
		else
			controllable:Pathto(sense:getViolationLocation().x,sense:getViolationLocation().y)
		end

end

-- function victimize(controllable,sense,script)
	-- pos=controllable:getPosition()
	-- victim=sense:getVictim(controllable,8,true,"Ruffian",false)
	-- if not (victim==nil) then
		-- if pos:getDistance(victim:getPosition())<2 then
				-- controllable:startVoreScript("warden_ruffian_OV", victim)
		-- else
			-- if controllable:HasPath() then
				-- controllable:FollowPath()
			-- else
				-- controllable:Pathto(victim:getPosition().x,victim:getPosition().y,8)
			-- end		
		-- end	
	-- end
-- end

function main(controllable, sense, script)  
	hostile=sense:getHostile(controllable,10,true)
	if not (hostile == nil ) and not controllable:getPeace() then
		combat(controllable,sense,script,hostile)
	else 
		if sense:getViolationLevel() > 0 then
		pursue(controllable,sense,script)
		else
			-- if not victimize(controllable,sense,script) then
				a=math.random(0,8)
				if (a==8) then
					controllable:attemptObserverVore();
				else
					controllable:move(a);
				end
			-- end
		end
	end  
end