create table users( 
id int auto_increment primary key,
user_uid varchar(255) not null,
email varchar(50) not null, 
encrypted_pwd varchar(255) not null, 
phone_num varchar(255) not null, 
nickname varchar(50) not null, 
user_lock boolean not null, 
disabled boolean not null, 
coin integer not null, 
ticket integer not null, 
profile_image varchar(255) not null, 
profile_image_icon varchar(255) not null, 
birthdate varchar(255) not null, 
create_time_at timestamp not null, 
credit_enabled boolean not null, 
credit_time timestamp,
credit_number integer, 
credit_pwd varchar(255), 
user_grade varchar(255) not null, 
grade_start timestamp not null, 
grade_end timestamp not null, 
login_count integer not null, 
recent_login_time timestamp not null, 
matching_time timestamp not null, 
next_matching_time timestamp not null, 
reset_matching boolean not null
);

create table userdetails(
id int auto_increment primary key, 
user_uid varchar(255) not null, 
user_group integer, 
gender boolean not null, 
otherm boolean default true not null, 
otherw boolean default true not null, 
user_age integer not null, 
user_grade varchar(255) not null, 
grade_end timestamp, 
grade_start timestamp not null, 
user_lock boolean not null, 
disabled boolean not null, 
respect integer not null, 
sex integer not null, 
communication integer not null, 
contact integer not null, 
date integer not null, 
happy varchar(500) not null, 
dating varchar(500) not null, 
work varchar(500) not null, 
latitude double not null, 
locationkor varchar(255) null, 
locationeng varchar(255) null, 
longitude double not null, 
sub_latitude double, 
sub_locationkor varchar(255), 
sub_locationeng varchar(255), 
sub_longitude double, 
max_age integer not null, 
min_age integer not null, 
max_distance integer not null, 
recent_login_time timestamp not null
);

create table usergrades(
id int auto_increment primary key,
disabled boolean not null, 
grade_date integer not null, 
grade_percent varchar(255) not null, 
grade_type varchar(255) not null, 
grade_uid varchar(255) not null
);

create table totalusers (
id int auto_increment primary key,
disabled boolean not null, 
totaluser integer not null,
created_at timestamp not null
);

create table certifications(
id int auto_increment primary key, 
certification_uid varchar(255) not null,
disabled boolean not null, 
phone_num varchar(255) not null, 
credit_number integer, 
created_at timestamp not null
);

create table lockusers(
id int auto_increment primary key, 
lock_uid varchar(255) not null,
lock_user_uid varchar(255) not null,
lock_type varchar(100) not null,
lock_reasons varchar(300) not null,
refer_uid varchar(255) not null,
from_user_uid varchar(255) not null,
lock_user_disabled boolean not null, 
active_time timestamp not null
);

create table rateplans(
id int auto_increment primary key,
rateplan_id varchar(255) not null,
rateplan_type varchar(255) not null, 
product_id varchar(255) not null, 
months int not null,
disabled boolean not null
);

create table purchases(
id int auto_increment primary key,
purchaseuid varchar(255) not null,
user_uid varchar(255) not null, 
order_id varchar(255) not null, 
package_name varchar(255) not null, 
product_id varchar(255) not null, 
purchase_time varchar(255) not null, 
purchase_state varchar(255) not null, 
purchase_token varchar(255) not null, 
acknowledged boolean not null
);

create table blockusers(
id int auto_increment primary key,
block_uid varchar(255) not null, 
user_uid varchar(255) not null, 
phone_num varchar(255) not null, 
block_time timestamp not null, 
disabled boolean not null
);

create table posts(
id int auto_increment primary key,
post_id varchar(255) not null, 
post_uid varchar(255) not null, 
post_image varchar(255) null, 
locationeng varchar(255) null, 
locationkor varchar(255) null, 
post_contents varchar(255) null,
post_like_count int not null,
post_comment_count int not null,
post_time timestamp not null, 
disabled boolean not null
);

create table hashtags(
id int auto_increment primary key,
hashtag_id varchar(255) not null, 
post_id varchar(255) not null, 
post_uid varchar(255) null, 
hashtag_name varchar(255) null, 
hashtag_time timestamp not null, 
disabled boolean not null
);
