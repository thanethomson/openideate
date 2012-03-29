
class IdeaVersionAlreadySaved(Exception):
    """
    A generic exception class to raise when an idea version has already been
    saved. Idea versions can only be saved once. 
    """
    pass


class IdeaVersionNotChanged(Exception):
    """
    Raised when a new version is created but has no changes from the
    previous one.
    """
    pass


class IdeaAdminPrivilegeRequired(Exception):
    pass


class IdeaReadPrivilegeRequired(Exception):
    pass


class IdeaWritePrivilegeRequired(Exception):
    pass

