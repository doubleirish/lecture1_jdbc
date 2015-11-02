SELECT u.id, u.user_name, u.first_name ,u.last_name, u.active_since
       , a.street, a.city , a.state , a.zip
       , p.label , p.phone 
FROM users u
LEFT OUTER JOIN address a on  a.id = u.id 
LEFT OUTER JOIN phone p on  p.user_Id = u.id 
ORDER BY u.id;  