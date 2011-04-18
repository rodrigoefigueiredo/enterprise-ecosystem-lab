insert into user_authorities (user_id, authority)
select id, 'ROLE_USER'
from users
where not exists (
  select 1
  from user_authorities
  where user_authorities.user_id = users.id
    and user_authorities.authority = 'ROLE_USER'
);
