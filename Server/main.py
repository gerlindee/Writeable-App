class User:
    def __init__(self, uid, display_name, avatar_url, email, password):
        self.__uid = uid
        self.__display_name = display_name
        self.__avatar_url = avatar_url
        self.__email = email
        self.__password = password

    def get_uid(self):
        return self.__uid

    def get_display_name(self):
        return self.__display_name

    def get_avatar_url(self):
        return self.__avatar_url

    def get_email(self):
        return self.__email

    def get_password(self):
        return self.__password

    def set_display_name(self, new_display_name):
        self.__display_name = new_display_name

    def set_avatar_url(self, new_avatar_url):
        self.__avatar_url = new_avatar_url

    def set_email(self, new_email):
        self.__email = new_email

    def set_password(self, new_password):
        self.__password = new_password