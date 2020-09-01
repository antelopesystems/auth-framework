package com.antelopesystem.authframework.token.exception

import com.antelopesystem.authframework.authentication.RequestFailedException


class InvalidTokenException: RequestFailedException("Invalid authentication token")